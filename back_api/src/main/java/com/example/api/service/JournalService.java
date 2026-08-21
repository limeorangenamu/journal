package com.example.api.service;

import com.example.api.dto.*;
import com.example.api.entity.Journal;
import com.example.api.entity.Members;
import com.example.api.entity.Photos;

import java.util.*;
import java.util.stream.Collectors;

public interface JournalService {
    Long register(JournalDTO journalDTO);

    PageResultDTO<JournalDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

    PageResultDTO<JournalDTO, Object[]> getMyList(PageRequestDTO pageRequestDTO, Long mid);

    PageResultDTO<JournalDTO, Object[]> getPublicList(PageRequestDTO pageRequestDTO);

    JournalDTO get(Long jno);

    void modify(JournalDTO journalDTO);

    RemoveJournalResponseDTO removeJournalWithCommentsAndPhotos(Long jno, PageRequestDTO pageRequestDTO);

    void removePhotosByUUID(String uuid); // uuid로 photos 삭제

    default Map<String, Object> dtoToEntity(JournalDTO journalDTO) {
        // journal, imgList라는 키를 가질 수 있도록 선언
        Map<String, Object> map = new HashMap<>();
        Journal journal = Journal.builder()
                .jno(journalDTO.getJno())
                .title(journalDTO.getTitle())
                .content(journalDTO.getContent())
                .isPublic(journalDTO.isPublic())
                .members(Members.builder().mid(journalDTO.getMembersDTO().getMid()).build())
                .build();
        map.put("journal", journal);
        List<PhotosDTO> photosDTOList = journalDTO.getPhotosDTOList();

        // imageDTOList에 이미지가 있다면
        if (photosDTOList != null && photosDTOList.size() > 0) {
            List<Photos> photosList = photosDTOList.stream().map(
                    photosDTO -> Photos.builder()
                            .path(photosDTO.getPath())
                            .photosName(photosDTO.getPhotosName())
                            .uuid(photosDTO.getUuid())
                            .journal(journal)
                            .build()
            ).collect(Collectors.toList());
            map.put("photosList", photosList);
        }
        return map;
    }

    default JournalDTO entityToDto(Journal journal, List<Photos> photosList, Members members, Long likes, Long commentsCnt) {
        MembersDTO membersDTO = MembersDTO.builder()
                .mid(members.getMid())
                .name(members.getName())
                .email(members.getEmail())
                .nickname(members.getNickname())
                .mobile(members.getMobile())
                .build();
        JournalDTO journalDTO = JournalDTO.builder()
                .jno(journal.getJno())
                .title(journal.getTitle())
                .content(journal.getContent())
                .isPublic(journal.isPublic())
                .membersDTO(membersDTO)
                .regDate(journal.getRegDate())
                .modDate(journal.getModDate())
                .build();
        List<PhotosDTO> photosDTOList =
                (photosList == null || photosList.isEmpty()) ? new ArrayList<>() :
                        photosList.stream().filter(Objects::nonNull)
                                .map(
                                        photos -> PhotosDTO.builder()
                                                .path(photos.getPath())
                                                .photosName(photos.getPhotosName())
                                                .uuid(photos.getUuid())
                                                .build()
                                ).collect(Collectors.toList());
        journalDTO.setPhotosDTOList(photosDTOList);
        journalDTO.setLikes(likes);
        journalDTO.setCommentsCnt(commentsCnt);
        return journalDTO;
    }
}
