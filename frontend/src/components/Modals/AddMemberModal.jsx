import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";
import styles from "@/styles/Modals/AddMemberModal.module.css";
import { updateImageFile } from "@/services/fileAPI";
import { registerUser } from "@/services/userAPI";
import { useState } from "react";
import ImageUpload from "@/components/ImageUpload";

const AddMemberModal = ({ onSubmit, onClose }) => {
  const [selectedImage, setSelectedImage] = useState();
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [position, setPosition] = useState("");
  const [duty, setDuty] = useState("");
  const [employeeNumber, setEmployeeNumber] = useState("");

  const handleImageSelect = (file) => {
    setSelectedImage(file);
  };

  const handleClickSubmit = async () => {
    const userData = {
      name: name,
      password: password,
      email: email,
      position: position,
      duty: duty,
      employeeNumber: employeeNumber,
      profileImageUrl: selectedImage,
    };

    try {
      const maxFileSize = 5 * 1024 * 1024; // 5MB
      if (!selectedImage) {
        alert("이미지를 선택해주세요.");
        return;
      } else if (selectedImage.size > maxFileSize) {
        alert("이미지 파일 크기는 5MB 이하여야 합니다.");
        return;
      }
      try {
        const response = await updateImageFile(selectedImage);
        const imageUrl = response.data;
        userData.profileImageUrl = imageUrl;
        if (response.status === 200 || response.status === 201) {
          console.log(userData);
          const temp = await registerUser(userData);
          console.log(temp);
          if (temp.status === 201) {
            alert("사원 등록이 완료되었습니다.");
            onSubmit();
          } else {
            alert("사원 등록에 실패했습니다.");
          }
        }
      } catch (error) {
        console.error("이미지 업로드 실패:", error);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const handleClickCancel = () => {
    onClose();
  };

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
        <h1 className={styles.title}>사원 정보 등록</h1>
        <div className={styles.imageBox}>
          <ImageUpload
            classNameValue={styles.image}
            onImageSelect={handleImageSelect}
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
          <label htmlFor="password" className={styles.text}>
            비밀번호
          </label>
          <input
            type="password"
            id="password"
            className={styles.input}
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
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
          <label htmlFor="email" className={styles.text}>
            이메일
          </label>
          <input
            type="text"
            id="email"
            className={styles.input}
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
            }}
          />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="employeeNumber" className={styles.text}>
            사원번호
          </label>
          <input
            type="text"
            id="employeeNumber"
            className={styles.input}
            value={employeeNumber}
            onChange={(e) => {
              setEmployeeNumber(e.target.value);
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

AddMemberModal.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};
export default AddMemberModal;
