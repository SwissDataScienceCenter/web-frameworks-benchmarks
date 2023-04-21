import http from "k6/http";
import { check, fail } from "k6";

export function testDirect(url) {
  const res = http.get(url);
  if (
    !check(res, {
      "request succeeded with 200": (res) =>
        res.status == 200,
    })
  ) {
    fail(
      `request failed with status code ${res.status} and response ${res.body}`
    );
  }
  return res;
}

export const directOptions = {
  scenarios: {
    testUploads: {
      executor: "per-vu-iterations",
      vus: 100,
      iterations: 10,
    },
  },
};
