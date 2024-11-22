import { useState } from "react";
import Home from "@/assets/Menu/SSMART OFFICE.svg?react";
import Camera from "@/assets/Modals/Camera.svg?react";
import Profile from "@/assets/Common/Profile.png";

import useModalStore from "@/store/useModalStore";
import ChangePasswordModal from "@/components/Modals/ChangePasswordModal";
import ChangeInfoModal from "@/components/Modals/ChangeInfoModal";
import ChangeImageModal from "@/components/Modals/ChangeImageModal";

import styles from "@/styles/MyPage/MyInfo.module.css";
import useMyInfoStore from "@/store/useMyInfoStore";
import { fetchMyWelfarePoint } from "@/services/myInfoAPI";
import { useEffect } from "react";

const MyInfo = () => {
  const openModal = useModalStore((state) => state.openModal);
  const { name, email, duty, profileImageUrl, phoneNumber } =
    useMyInfoStore();
  const [welfarePoint, setWelfarePoint] = useState(0);

  const hadleChangeImageClick = () => {
    openModal(ChangeImageModal, {
      onSubmit: () => {},
    });
  };
  const hadleChangePasswordClick = () => {
    openModal(ChangePasswordModal, {
      onSubmit: () => {},
    });
  };
  const hadleChangeInfoClick = () => {
    openModal(ChangeInfoModal, {
      onSubmit: () => {},
    });
  };

  useEffect(() => {
    const getMyPoint = async () => {
      const point = await fetchMyWelfarePoint();
      setWelfarePoint(point);
    };
    getMyPoint();
  }, []);

  // 포인트에 콤마 붙이기
  const formatWithCommas = (num) => {
    if (typeof num !== "number" || typeof num === "string") {
      return 0;
    }
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  };

  return (
    <div className={styles.container}>
      <div className={styles.left}>
        <Home className={styles.image} />
      </div>
      <div className={styles.right}>
        <div className={styles.imageBox}>
          <div className={styles.subBox}>
            <img
              src={profileImageUrl ? profileImageUrl : Profile}
              alt="이미지"
              className={styles.profileImage}
            />
            <button
              className={styles.changeImage}
              onClick={hadleChangeImageClick}
            >
              <Camera className={styles.cameraImage} />
            </button>
          </div>
        </div>
        <div className={styles.content}>
          <div className={styles.title}>
            <p className={styles.subTitle}>
              {name} {duty}님
            </p>
            <p className={styles.subTitle1}>반갑습니다!</p>
          </div>
          <div className={styles.subTitle1}>{email}</div>
          <div className={styles.subTitle1}>
            {phoneNumber ? phoneNumber : "연락처를 등록해주세요"}
          </div>
          <div className={styles.welfare}>
            <p className={styles.subWelfare}>복지포인트 : </p>
            <p className={styles.subWelfare1}>
              {formatWithCommas(welfarePoint)}
            </p>
          </div>
          <div className={styles.buttonBox}>
            <button
              className={`${styles.modifyButton} ${styles.button}`}
              onClick={hadleChangeInfoClick}
            >
              개인정보 수정
            </button>
            <button
              className={`${styles.changeButton} ${styles.button}`}
              onClick={hadleChangePasswordClick}
            >
              비밀번호 변경
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
export default MyInfo;
