import Swal from "sweetalert2";

export const handleSuccess = (message, title = "성공") => {
  // export const handleSuccess = (message, options = {}) => {
  Swal.fire({
    icon: "success",
    title: title,
    text: message,
    confirmButtonText: "확인",
    timer: 3000,
    timerProgressBar: true,
  });
};
