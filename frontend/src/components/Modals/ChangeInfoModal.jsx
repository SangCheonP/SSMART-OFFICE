import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";

import styles from "@/styles/Modals/ChangeInfoModal.module.css";
import useMyInfoStore from "@/store/useMyInfoStore";
import { useState } from "react";
import { updateTelNumber } from "@/services/myInfoAPI";

import { handleSuccess } from "@/utils/successHandler";
import Swal from "sweetalert2";

const ChangeInfoModal = ({ onSubmit, onClose }) => {
  const updatePhoneNumber = useMyInfoStore((state) => state.updatePhoneNumber);
  const { name, email, phoneNumber } = useMyInfoStore();
  const [telNumber, setTelNumber] = useState(phoneNumber);

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

  // 입력 핸들러
  const handleInputChange = (e) => {
    const formattedNumber = formatPhoneNumber(e.target.value);
    setTelNumber(formattedNumber);
  };

  // 변경 버튼
  const handleClickSubmit = async () => {
    const digits = telNumber.replace(/\D/g, "");
    if (digits.length < 10 || digits.length > 11) {
      Swal.fire({
        icon: "error",
        title: "연락처 입력 오류",
        showConfirmButton: false,
        timer: 1500,
        text: "연락처는 10자 이상의 숫자만 입력가능합니다.",
      });
      return;
    }
    await updateTelNumber(telNumber);
    updatePhoneNumber(telNumber);
    handleSuccess("개인정보 수정 성공!");
    onSubmit();
  };

  // 모달 닫기 버튼
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
          height: "500px",
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
        <h1 className={styles.title}>개인정보 수정</h1>
        <div>
          <div className={styles.text}>이름</div>
          <input
            type="text"
            className={`${styles.input} ${styles.readOnly}`}
            readOnly
            value={name}
          />
        </div>
        <div>
          <div className={styles.text}>이메일</div>
          <input
            type="text"
            className={`${styles.input} ${styles.readOnly}`}
            value={email}
            readOnly
          />
        </div>
        <div>
          <div className={styles.text}>휴대전화</div>
          <input
            type="text"
            className={styles.input}
            placeholder="연락처를 입력해주세요"
            value={telNumber}
            onChange={handleInputChange}
            maxLength={13}
          />
        </div>
        <div className={styles.buttonBox}>
          <button
            onClick={handleClickSubmit}
            className={`${styles.confirm} ${styles.button}`}
          >
            수정
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

ChangeInfoModal.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ChangeInfoModal;
