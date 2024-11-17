import api from "@/services/api";
import useMyInfoStore from "@/store/useMyInfoStore";
import { handleError } from "@/utils/errorHandler";

// 비밀번호 업데이트
export const updatePassword = async (currentPassword, newPassword) => {
  try {
    const response = await api.patch("/users/me/password", {
      oldPassword: currentPassword,
      newPassword: newPassword,
    });
    return response.data;
  } catch (e) {
    handleError(e);
  }
};

// 프로필 이미지 업데이트
export const updateProfile = (profileImage) => {
  try {
    const response = api.patch("/users/me", {
      profileImageUrl: profileImage,
    });
    return response;
  } catch (e) {
    handleError(e);
  }
};

// 핸드폰 번호 업데이트
export const updateTelNumber = async (phoneNumber) => {
  try {
    api.patch("/users/me", {
      phoneNumber: phoneNumber,
    });
  } catch (e) {
    handleError(e);
  }
};

// 내 정보 가져오기
export const fetchMyInfo = async () => {
  try {
    const response = await api.get("/users/me");
    useMyInfoStore.getState().setMyInfoData(response.data.data);
  } catch (e) {
    handleError(e);
  }
};

export const fetchMyWelfarePointList = async ({
  startDate,
  endDate,
  page = 0,
  size = 10,
  sort = "id,desc",
}) => {
  try {
    const response = await api.get("/points/history", {
      params: {
        startDate,
        endDate,
        page,
        size,
        sort,
      },
    });
    return response.data;
  } catch (e) {
    handleError(e);
  }
};

// 내 포인트 조회
export const fetchMyWelfarePoint = async () => {
  try {
    const response = await api.get("/points");
    return response.data.data.balance;
  } catch (e) {
    handleError(e);
  }
};
