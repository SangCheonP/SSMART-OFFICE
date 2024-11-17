import Swal from "sweetalert2";

export const handleError = (error) => {
  if (error.response) {
    const { status, data } = error.response;

    // 서버에서 제공한 에러 메시지
    const errorMessage =
      data?.message || "알 수 없는 서버 에러가 발생했습니다.";
    console.error(`에러 상태 코드: ${status}, 메시지: ${errorMessage}`);

    // 상태 코드별 SweetAlert로 사용자 피드백 제공
    switch (status) {
      // case 400:
      //   Swal.fire({
      //     icon: "error",
      //     title: "잘못된 요청",
      //     timer: 2000,
      //     text: "입력 정보를 확인하세요.",
      //   });
      //   break;
      // case 401:
      //   Swal.fire({
      //     icon: "warning",
      //     title: "인증 필요",
      //     timer: 2000,
      //     text: "인증이 만료되었습니다. 다시 로그인하세요.",
      //   }).then(() => {
      //     window.location.href = "/login";
      //   });
      //   break;
      // case 403:
      //   Swal.fire({
      //     icon: "error",
      //     title: "접근 권한 없음",
      //     timer: 2000,
      //     text: "이 작업을 수행할 권한이 없습니다.",
      //   });
      //   break;
      // case 404:
      //   Swal.fire({
      //     icon: "info",
      //     title: "정보 없음",
      //     timer: 2000,
      //     text: "요청한 정보를 찾을 수 없습니다.",
      //   });
      //   break;
      // case 500:
      //   Swal.fire({
      //     icon: "error",
      //     title: "서버 오류",
      //     timer: 2000,
      //     text: "서버에 문제가 발생했습니다. 관리자에게 문의하세요.",
      //   });
      //   break;
      default:
        Swal.fire({
          icon: "error",
          title: "오류 발생",
          timer: 2000,
          text: `${errorMessage}`,
        });
    }
  } else if (error.request) {
    // 요청이 전송되었으나 응답이 없는 경우
    console.warn("응답이 없습니다. 네트워크 상태를 확인하세요.");
    Swal.fire({
      icon: "warning",
      title: "네트워크 오류",
      timer: 2000,
      text: "서버와 연결할 수 없습니다. 인터넷 상태를 확인하세요.",
    });
  } else {
    // 요청 설정 중 발생한 에러
    console.error("요청 설정 오류:", error.message);
    Swal.fire({
      icon: "error",
      title: "요청 오류",
      timer: 2000,
      text: `요청 처리 중 문제가 발생했습니다: ${error.message}`,
    });
  }

  return Promise.reject(error); // 에러를 그대로 반환해 호출한 쪽에서 추가 처리 가능
};
