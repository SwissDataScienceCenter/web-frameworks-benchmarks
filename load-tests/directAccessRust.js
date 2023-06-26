import { testDirect, directOptions } from "./common.js";

export const options = directOptions;

export default function test() {
  testDirect("http://localhost:3010");
}
