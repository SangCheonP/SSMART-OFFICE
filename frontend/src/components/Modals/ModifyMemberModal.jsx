import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";
import styles from "@/styles/Modals/ModifyMemberModal.module.css";
import { updateImageFile } from "@/services/fileAPI";
import { getUser, modifyUser } from "@/services/userAPI";
import { useEffect, useState } from "react";
import ImageUpload from "@/components/ImageUpload";

const ModifyMemberModal = ({ userId, onSubmit, onClose }) => {
  const [selectedImage, setSelectedImage] = useState();
  const [isImageFile, setIsImageFile] = useState(false);
  const [name, setName] = useState("");
  const [position, setPosition] = useState("");
  const [duty, setDuty] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");

  const handleImageSelect = (file) => {
    setSelectedImage(file);
    setIsImageFile(file instanceof File);
  };

  const handleClickSubmit = async () => {
    const userData = {
      name: name,
      position: position,
      duty: duty,
      profileImageUrl: selectedImage,
      phoneNumber: phoneNumber,
    };

    try {
      if (isImageFile) {
        const maxFileSize = 5 * 1024 * 1024; // 5MB
        if (!selectedImage) {
          alert("이미지를 선택해주세요.");
          return;
        } else if (selectedImage.size > maxFileSize) {
          alert("이미지 파일 크기는 5MB 이하여야 합니다.");
          return;
        }
        const response = await updateImageFile(selectedImage);
        const imageUrl = response.data;
        userData.profileImageUrl = imageUrl;
      }
      try {
        const temp = await modifyUser(userId, userData);
        if (temp.status === 200) {
          alert("수정이 완료되었습니다.");
          onSubmit();
        } else {
          alert("수정에 실패했습니다.");
        }
      } catch (error) {
        console.error("제출 중 오류 발생:", error);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const handleClickCancel = () => {
    onClose();
  };

  useEffect(() => {
    getUser(userId)
      .then((response) => {
        setName(response.data.name);
        setPosition(response.data.position);
        setDuty(response.data.duty);
        setSelectedImage(response.data.profileImageUrl);
        setPhoneNumber(response.data.phoneNumber);
      })
      .catch((error) => {
        console.error(error);
      });
  }, [userId]);

  return (
    <ReactModal
      isOpen={true}
      style={{
        overlay: {
          position: "fixed",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: "rgba(0, 0, 0, 0.75)",
          zIndex: 10,
        },
        content: {
          position: "absolute",
          width: "400px",
          height: "600px",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          background: "#fff",
          overflow: "auto",
          WebkitOverflowScrolling: "touch",
          borderRadius: "20px",
          outline: "none",
          padding: "20px",
        },
      }}
    >
      <button className={styles.close} onClick={handleClickCancel}>
        <Close />
      </button>
      <div className={styles.container}>
        <h1 className={styles.title}>사원 정보 수정</h1>
        <div className={styles.imageBox}>
          <ImageUpload
            classNameValue={styles.image}
            onImageSelect={handleImageSelect}
            defaultImage={selectedImage}
          />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="name" className={styles.text}>
            이름
          </label>
          <input
            type="text"
            id="name"
            className={styles.input}
            value={name}
            onChange={(e) => {
              setName(e.target.value);
            }}
          />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="position" className={styles.text}>
            직책
          </label>
          <input
            type="text"
            id="position"
            className={styles.input}
            value={position}
            onChange={(e) => {
              setPosition(e.target.value);
            }}
          />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="position" className={styles.text}>
            직무
          </label>
          <input
            type="text"
            id="name"
            className={styles.input}
            value={duty}
            onChange={(e) => {
              setDuty(e.target.value);
            }}
          />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="phoneNumber" className={styles.text}>
            연락처
          </label>
          <input
            type="text"
            id="phoneNumber"
            className={styles.input}
            value={phoneNumber}
            onChange={(e) => {
              setPhoneNumber(e.target.value);
            }}
          />
        </div>
        <div className={styles.buttonBox}>
          <button
            onClick={handleClickSubmit}
            className={`${styles.confirm} ${styles.button}`}
          >
            등록
          </button>
          <button
            onClick={handleClickCancel}
            className={`${styles.cancel} ${styles.button}`}
          >
            취소
          </button>
        </div>
      </div>
    </ReactModal>
  );
};

ModifyMemberModal.propTypes = {
  userId: PropTypes.number,
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};
export default ModifyMemberModal;
