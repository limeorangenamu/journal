package com.example.api.service;

import com.example.api.dto.*;
import com.example.api.entity.Journal;
import com.example.api.entity.Members;
import com.example.api.entity.Photos;
import com.example.api.repository.CommentsRepository;
import com.example.api.repository.JournalRepository;
import com.example.api.repository.PhotosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {
    private final JournalRepository journalRepository;
    private final PhotosRepository photosRepository;
    private final CommentsRepository commentsRepository;

    @Value("${com.example.upload.path}")
    private String uploadPath;

    @Override
    public Long register(JournalDTO journalDTO) {
        Map<String, Object> map = dtoToEntity(journalDTO);
        Journal journal = (Journal) map.get("journal");
        List<Photos> photosList = (List<Photos>) map.get("photosList");
        journalRepository.save(journal);
        if (photosList != null && !photosList.isEmpty()) {
            photosRepository.saveAll(photosList);
        }
        return journal.getJno();
    }

    @Override
    public PageResultDTO<JournalDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("jno").descending());
        Page<Object[]> result = journalRepository.searchPage(
                pageRequestDTO.getType(), pageRequestDTO.getKeyword(), pageable);
        Function<Object[], JournalDTO> function = arr -> entityToDto(
                (Journal) arr[0],
                (List<Photos>) (Arrays.asList((Photos) arr[1])),
                (Members) arr[2],
                (Long) arr[3], (Long) arr[4]
        );
        return new PageResultDTO<>(result, function);
    }

    @Override
    public JournalDTO get(Long jno) {
        List<Object[]> result = journalRepository.getJournalDetail(jno);
        Journal journal = (Journal) result.get(0)[0];
        List<Photos> photosList = new ArrayList<>();
        result.forEach(row -> {
            Photos photo = (Photos) row[1];
            if (photo != null) {photosList.add(photo);}
        });
        Members members = (Members) result.get(0)[2];
        Long likes = (Long) result.get(0)[3];
        Long commentCnt = (Long) result.get(0)[4];
        return entityToDto(journal, photosList, members, likes, commentCnt);
    }

    @Transactional
    @Override
    public void modify(JournalDTO journalDTO) {
        Optional<Journal> result = journalRepository.findById(journalDTO.getJno());
        if (result.isPresent()) {
            journalDTO.setMembersDTO(MembersDTO.builder().mid(result.get().getMembers().getMid()).build());
            Map<String, Object> entityMap = dtoToEntity(journalDTO);
            Journal journal = (Journal) entityMap.get("journal");
            journal.changeTitle(journalDTO.getTitle());
            journal.changeContent(journalDTO.getContent());
            journalRepository.save(journal);

            List<Photos> newPhotosList = (List<Photos>) entityMap.get("photosList");//from client(new)
            List<Photos> originalPhotosList = photosRepository.findByJno(journal.getJno());//from db(origin)

            if (newPhotosList == null || newPhotosList.size() == 0) {
                // 수정창에서 이미지 모두를 지웠을 때
                photosRepository.deleteByJno(journal.getJno());
                for (int i = 0; i < originalPhotosList.size(); i++) {
                    Photos oldPhotos = originalPhotosList.get(i);
                    String fileName = oldPhotos.getPath() + File.separator
                            + oldPhotos.getUuid() + "_" + oldPhotos.getPhotosName();
                    deleteFile(fileName);
                }
            } else { // newPhotosList에 일부 변화 발생
                newPhotosList.forEach(photos -> {
                    boolean result1 = false;
                    for (int i = 0; i < originalPhotosList.size(); i++) {
                        result1 = originalPhotosList.get(i).getUuid().equals(photos.getUuid());
                        if (result1) break;
                    }
                    if (!result1) photosRepository.save(photos);
                });
                originalPhotosList.forEach(oldPhotos -> {
                    boolean result1 = false;
                    for (int i = 0; i < newPhotosList.size(); i++) {
                        result1 = newPhotosList.get(i).getUuid().equals(oldPhotos.getUuid());
                        if (result1) break;
                    }
                    if (!result1) {
                        photosRepository.deleteByUuid(oldPhotos.getUuid());
                        String fileName = oldPhotos.getPath() + File.separator
                                + oldPhotos.getUuid() + "_" + oldPhotos.getPhotosName();
                        deleteFile(fileName);
                    }
                });
            }
        }
    }

    private void deleteFile(String fileName) {
        try {
            File file = new File(uploadPath, URLDecoder.decode(fileName, "UTF-8"));
            if (file.exists() && !file.delete()) {
                log.warn("삭제 실패 : {}", file.getAbsolutePath());
            }
            File thumbnail = new File(file.getParent(), "s_" + file.getName());
            if (thumbnail.exists() && !thumbnail.delete()) {
                log.warn("썸네일 삭제 실패 : {}", thumbnail.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public RemoveJournalResponseDTO removeJournalWithCommentsAndPhotos(Long jno, PageRequestDTO pageRequestDTO) {
        deleteJournalFiles(jno);
        photosRepository.deleteByJno(jno);
        commentsRepository.deleteByJno(jno);
        journalRepository.deleteById(jno);
        adjustPage(pageRequestDTO);
        return RemoveJournalResponseDTO.builder()
                .jno(jno)
                .page(pageRequestDTO.getPage())
                .type(pageRequestDTO.getType())
                .keyword(pageRequestDTO.getKeyword())
                .message("삭제 완료.")
                .build();
    }

    private void adjustPage(PageRequestDTO pageRequestDTO) {
        if (pageRequestDTO.getPage() == 1) return;
        if (getList(pageRequestDTO).getDtoList().isEmpty())
            pageRequestDTO.setPage(pageRequestDTO.getPage() - 1);
    }

    private void deleteJournalFiles(Long jno) {
        List<Photos> photosList = photosRepository.findByJno(jno);
        photosList.forEach(photos -> {
            String fileName = photos.getPath() + File.separator + photos.getUuid() + "_" + photos.getPhotosName();
            deleteFile(fileName);
        });
    }

    @Override
    public void removePhotosByUUID(String uuid) {
        photosRepository.deleteByUuid(uuid);
    }
}
