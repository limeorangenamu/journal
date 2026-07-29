export const makeArray = (length: number) => new Array(length).fill(null)

export const range = function (min: number, max: number): number[] {
  return makeArray(max - min).map((notUsed, index) => index + min)
}

export const random = function (min: number, max: number) {
  return Math.floor(Math.random() * (max - min)) + min
}
