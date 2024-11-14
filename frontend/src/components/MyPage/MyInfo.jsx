import Home from "@/assets/Menu/SSMART OFFICE.svg?react";
import Camera from "@/assets/Modals/Camera.svg?react";
import Profile from "@/assets/Common/Profile.png";

import useModalStore from "@/store/useModalStore";
import ChangePasswordModal from "@/components/Modals/ChangePasswordModal";
import ChangeInfoModal from "@/components/Modals/ChangeInfoModal";
import ChangeImageModal from "@/components/Modals/ChangeImageModal";

import styles from "@/styles/MyPage/MyInfo.module.css";
import useMyInfoStore from "@/store/useMyInfoStore";

const MyInfo = () => {
  const openModal = useModalStore((state) => state.openModal);
  const { name, email, position, profileImage, phoneNumber } = useMyInfoStore();

  const hadleChangeImageClick = () => {
    openModal(ChangeImageModal, {
      onSubmit: () => {
        console.log("비밀번호 수정 모달입니다.");
      },
    });
  };
  const hadleChangePasswordClick = () => {
    openModal(ChangePasswordModal, {
      onSubmit: () => {
        console.log("비밀번호 수정 모달입니다.");
      },
    });
  };
  const hadleChangeInfoClick = () => {
    openModal(ChangeInfoModal, {
      onSubmit: () => {
        console.log("정보수정 모달입니다.");
      },
    });
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
              src={profileImage ? profileImage : Profile}
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
              {name} {position}님
            </p>
            <p className={styles.subTitle1}>반갑습니다!</p>
          </div>
          <div>{email}</div>
          <div>{phoneNumber ? phoneNumber : "연락처를 등록해주세요"}</div>
          <div className={styles.welfare}>
            <p className={styles.subWelfare}>복지포인트 : </p>
            <p className={styles.subWelfare1}>34023</p>
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
