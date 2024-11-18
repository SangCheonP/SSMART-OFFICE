import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";
import styles from "@/styles/Modals/AddMemberModal.module.css";
import { updateImageFile } from "@/services/fileAPI";
import { registerUser } from "@/services/userAPI";
import { useRef, useState } from "react";
import ImageUpload from "@/components/ImageUpload";

import { handleError } from "@/utils/errorHandler";

import Swal from "sweetalert2";

const AddMemberModal = ({ onSubmit, onClose }) => {
  const [selectedImage, setSelectedImage] = useState();
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [position, setPosition] = useState("");
  const [duty, setDuty] = useState("");
  const [employeeNumber, setEmployeeNumber] = useState("");

  const nameRef = useRef();
  const passwordRef = useRef();
  const emailRef = useRef();
  const positionRef = useRef();
  const dutyRef = useRef();
  const employeeNumberRef = useRef();

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

    if (!userData.name) {
      Swal.fire({
        icon: "error",
        title: "이름 입력 오류",
        text: "이름을 입력해주세요.",
        showConfirmButton: false,
        timer: 1500,
        didClose: () => {
          nameRef.current.focus();
        },
      });
      return;
    }
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{4,20}$/;
    if (!userData.password || !passwordRegex.test(userData.password)) {
      Swal.fire({
        icon: "error",
        title: "비밀번호 입력 오류",
        showConfirmButton: false,
        timer: 2000,
        text: "비밀번호는 4-20자 이내의 영문 숫자 조합이어야 합니다.",
        didClose: () => {
          positionRef.current.focus();
        },
      });
      return;
    }

    if (!userData.position) {
      Swal.fire({
        icon: "error",
        title: "직책 입력 오류",
        text: "직책을 입력해주세요.",
        timer: 1500,
        showConfirmButton: false,
        didClose: () => {
          positionRef.current.focus();
        },
      });
      return;
    }
    if (!userData.duty) {
      Swal.fire({
        icon: "error",
        title: "직무 입력 오류",
        text: "직무를 입력해주세요.",
        timer: 1500,
        showConfirmButton: false,
        didClose: () => {
          dutyRef.current.focus();
        },
      });
      return;
    }
    const emailRegex = /^[^\s@]+@gmail\.com$/;
    if (!emailRegex.test(userData.email)) {
      Swal.fire({
        icon: "error",
        title: "이메일 입력 오류",
        text: "이메일 주소를 다시한번 확인해주세요.",
        timer: 1500,
        showConfirmButton: false,
        didClose: () => {
          emailRef.current.focus();
        },
      });
      return;
    }
    if (!userData.employeeNumber || !/^S\d{8}$/.test(userData.employeeNumber)) {
      Swal.fire({
        icon: "error",
        title: "사원번호 입력 오류",
        text: "유효한 사원번호를 입력해주세요. 예시) S24000000",
        timer: 1500,
        showConfirmButton: false,
        didClose: () => {
          employeeNumberRef.current.focus();
        },
      });
      return;
    }

    try {
      const maxFileSize = 5 * 1024 * 1024;
      if (!selectedImage) {
        Swal.fire({
          icon: "error",
          title: "프로필 이미지",
          text: "이미지를 선택해주세요.",
          timer: 1500,
          showConfirmButton: false,
        });
        return;
      } else if (selectedImage.size > maxFileSize) {
        Swal.fire({
          icon: "error",
          title: "이미지 용량 제한",
          text: "이미지는 5MB 이하여야합니다.",
          timer: 1500,
          showConfirmButton: false,
        });
        return;
      }
      try {
        const response = await updateImageFile(selectedImage);
        const imageUrl = response.data;
        userData.profileImageUrl = imageUrl;
        if (response.status === 200 || response.status === 201) {
          await registerUser(userData);
          onSubmit();
        }
      } catch (error) {
        handleError(error);
      }
    } catch (error) {
      handleError(error);
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
            ref={nameRef}
            className={styles.input}
            value={name}
            placeholder="이름을 입력해주세요"
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
            placeholder="비밀번호를 입력해주세요"
            ref={passwordRef}
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
            placeholder="직책을 입력해주세요"
            ref={positionRef}
            className={styles.input}
            value={position}
            onChange={(e) => {
              setPosition(e.target.value);
            }}
          />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="duty" className={styles.text}>
            직무
          </label>
          <input
            type="text"
            id="duty"
            placeholder="직무를 입력해주세요"
            ref={dutyRef}
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
            placeholder="이메일을 입력해주세요"
            ref={emailRef}
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
            placeholder="입력 예시.) S24000000"
            ref={employeeNumberRef}
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
