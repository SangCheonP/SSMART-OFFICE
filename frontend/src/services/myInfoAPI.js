import api from "@/services/api";
import useMyInfoStore from "@/store/useMyInfoStore";

// 비밀번호 업데이트
export const updatePassword = async (currentPassword, newPassword) => {
  try {
    const response = await api.patch("/users/me/password", {
      oldPassword: currentPassword,
      newPassword: newPassword,
    });
    return response.data;
  } catch (e) {
    console.error(e);
  }
};

// 프로필 이미지 업데이트
export const updateProfile = (profileImage) => {
  try {
    const response = api.patch("/users/me", {
      profileImageUrl: profileImage,
    });
    console.error("myInfoAPI : ", response);
    return response;
  } catch (e) {
    console.error(e);
  }
};

// 핸드폰 번호 업데이트
export const updateTelNumber = async (phoneNumber) => {
  try {
    const response = api.patch("/users/me", {
      phoneNumber: phoneNumber,
    });
    console.error(response);
  } catch (e) {
    console.error(e);
  }
};

// 내 정보 가져오기
export const fetchMyInfo = async () => {
  try {
    const response = await api.get("/users/me");
    console.log("드렁오니?");
    useMyInfoStore.getState().setMyInfoData(response.data.data);
  } catch (e) {
    console.error(e);
  }
};

// 포인트 사용내역 가져오기
// export const fetchMyWelfarePointList = async ({
//   startDate = "2024-11-15",
//   endDate = "2024-11-16",
//   page = 0,
//   size = 10,
//   sort = "transactionTime,desc",
// }) => {
//   try {
//     const response = await api.get("/points/history", {
//       params: {
//         startDate: startDate,
//         endDate: endDate,
//         page: page,
//         size: size,
//         sort: sort,
//       },
//     });
//     return response.data;
//   } catch (e) {
//     console.error(e);
//   }
// };
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
    console.log(response.data);
    return response.data;
  } catch (e) {
    console.error(e);
  }
};

// 내 포인트 조회
export const fetchMyWelfarePoint = async () => {
  try {
    const response = await api.get("/points");
    console.log(response.data.data.balance);
    return response.data.data.balance;
  } catch (e) {
    console.error(e);
  }
};
