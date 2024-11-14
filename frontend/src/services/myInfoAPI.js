import api from "@/services/api";
import useMyInfoStore from "@/store/useMyInfoStore";

// 비밀번호 업데이트
export const updatePassword = async (currentPassword, newPassword) => {
  try {
    const response = await api.post("/users/me/password", {
      oldPassword: currentPassword,
      newPassword: newPassword,
    });
    return response.data;
  } catch (e) {
    throw e.response ? e.response.data : new Error("비밀번호 변경 실패");
  }
};

// 프로필 이미지 업데이트
export const updateProfileImage = (profileImage) => {
  try {
    const response = api.patch("/users/me", {
      profileImageUrl: profileImage,
    });
    return response;
  } catch (e) {
    console.log(e);
  }
};

// 핸드폰 번호 업데이트
export const updateTelNumber = async (phoneNumber) => {
  console.log(phoneNumber);
  try {
    const response = api.patch("/users/me", {
      phoneNumber: phoneNumber,
    });
    console.log(response);
  } catch (e) {
    console.log(e);
  }
};

// 유저 등록
export const registerUser = () => {};

export const fetchMyInfo = async () => {
  try {
    const response = await api.get("/users/me");
    useMyInfoStore.getState().setMyInfoData(response.data.data);
  } catch (e) {
    console.log(e);
  }
};
