import api from "@/services/api";
import useMyInfoStore from "@/store/useMyInfoStore";

// 비밀번호 업데이트
export const updatePassword = async (currentPassword, newPassword) => {
  console.log(currentPassword, newPassword);
  try {
    const response = await api.patch("/users/me/password", {
      oldPassword: currentPassword,
      newPassword: newPassword,
    });
    console.log(response.data);
    return response.data;
  } catch (e) {
    throw e.response ? e.response.data : new Error("비밀번호 변경 실패");
  }
};

// 프로필 이미지 업데이트
export const updateProfile = (profileImage) => {
  try {
    const response = api.patch("/users/me", {
      profileImageUrl: profileImage,
    });
    console.log("myInfoAPI : ", response);
    return response;
  } catch (e) {
    console.log(e);
  }
};

// 핸드폰 번호 업데이트
export const updateTelNumber = async (phoneNumber) => {
  try {
    const response = api.patch("/users/me", {
      phoneNumber: phoneNumber,
    });
    console.log(response);
  } catch (e) {
    console.log(e);
  }
};

// 내 정보 가져오기
export const fetchMyInfo = async () => {
  try {
    const response = await api.get("/users/me");
    console.log("드렁오니?");
    useMyInfoStore.getState().setMyInfoData(response.data.data);
  } catch (e) {
    console.log(e);
  }
};
