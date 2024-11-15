import api from "@/services/api";

// 이미지 파일 등록
export const updateImageFile = async (file) => {
  const formData = new FormData();
  formData.append("file", file);
  const response = await api.post("/files/upload", formData);
  return response.data;
};
