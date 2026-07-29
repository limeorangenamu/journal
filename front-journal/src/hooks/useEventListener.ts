import {useEffect} from 'react'

// | 합집합타입 : 2개 이상의 타입 중 하나를 사용
// & 교차 타입  : 2개 이상의 타입을 결합하여 모두 사용

export const useEventListener = (
  target: EventTarget | null, // 대상
  type: string, //이벤트 명
  callback: EventListenerOrEventListenerObject | null //콜백
) => {
  useEffect(() => {
    // 둘다 true를 만족해야 함
    if (target && callback) {
      target.addEventListener(type, callback)
      return () => target.removeEventListener(type, callback)
    }
  }, [target, type, callback])
}
