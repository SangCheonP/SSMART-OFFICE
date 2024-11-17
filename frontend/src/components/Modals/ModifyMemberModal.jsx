import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";
import styles from "@/styles/Modals/ModifyMemberModal.module.css";
import { updateImageFile } from "@/services/fileAPI";
import { getUser, modifyUser } from "@/services/userAPI";
import { useEffect, useRef, useState } from "react";
import ImageUpload from "@/components/ImageUpload";

import { handleError } from "@/utils/errorHandler";
import { handleSuccess } from "@/utils/successHandler";

import Swal from "sweetalert2";

const ModifyMemberModal = ({ userId, onSubmit, onClose }) => {
  const [selectedImage, setSelectedImage] = useState();
  const [isImageFile, setIsImageFile] = useState(false);
  const [name, setName] = useState("");
  const [position, setPosition] = useState("");
  const [duty, setDuty] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");

  const nameRef = useRef();
  const positionRef = useRef();
  const dutyRef = useRef();
  const phoneNumberRef = useRef();

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
      const digits = (phoneNumber || "").replace(/\D/g, "");
      console.log(digits);
      if (digits.length < 10 || digits.length > 11) {
        Swal.fire({
          icon: "error",
          title: "연락처 입력 오류",
          showConfirmButton: false,
          timer: 1500,
          text: "연락처는 10자 이상의 숫자만 입력가능합니다.",
          didClose: () => {
            phoneNumberRef.current.focus();
          },
        });
        return;
      }
      if (isImageFile) {
        const maxFileSize = 5 * 1024 * 1024; // 5MB
        if (!selectedImage) {
          Swal.fire({
            icon: "error",
            title: "오류",
            text: "이미지를 선택해주세요.",
            showConfirmButton: false,
            timer: 1500,
          });
          return;
        } else if (selectedImage.size > maxFileSize) {
          Swal.fire({
            icon: "error",
            title: "오류",
            text: "이미지는 5MB 이하여야합니다.",
            showConfirmButton: false,
            timer: 1500,
          });
          return;
        }
        const response = await updateImageFile(selectedImage);
        const imageUrl = response.data;
        userData.profileImageUrl = imageUrl;
      }
      try {
        const temp = await modifyUser(userId, userData);
        if (temp.status === 200) {
          handleSuccess("수정이 완료되었습니다.");
          onSubmit();
        }
      } catch (error) {
        handleError(error);
      }
    } catch (error) {
      handleError(error);
    }
  };

  const formatPhoneNumber = (value) => {
    const digits = value.replace(/\D/g, ""); // 숫자만 추출
    if (digits.length <= 3) {
      return digits;
    }

    if (digits.length <= 7) {
      return `${digits.slice(0, 3)}-${digits.slice(3)}`;
    }

    if (digits.length === 10) {
      // 10자리: 010-XXX-XXXX
      return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(
        6,
        10
      )}`;
    }
    if (digits.length === 11) {
      // 11자리: 010-XXXX-XXXX
      return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(
        7,
        11
      )}`;
    }
    return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7, 11)}`;
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
            ref={nameRef}
            className={styles.input}
            placeholder="이름을 입력해주세요"
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
            ref={positionRef}
            placeholder="직책을 입력해주세요"
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
            ref={dutyRef}
            placeholder="직무를 입력해주세요"
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
            ref={phoneNumberRef}
            placeholder="연락처를 입력해주세요"
            className={styles.input}
            value={phoneNumber}
            onChange={(e) => {
              const formattedPhone = formatPhoneNumber(e.target.value);
              setPhoneNumber(formattedPhone);
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
