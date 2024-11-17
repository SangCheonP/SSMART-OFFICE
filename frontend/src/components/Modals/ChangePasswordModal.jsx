import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";

import styles from "@/styles/Modals/ChangePasswordModal.module.css";
import { useState } from "react";
import { updatePassword } from "@/services/myInfoAPI";

import { handleError } from "@/utils/errorHandler";
import { handleSuccess } from "@/utils/successHandler";
import Swal from "sweetalert2";

const ChangePasswordModal = ({ onSubmit, onClose }) => {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleClickSubmit = async () => {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{4,20}$/;
    if (!passwordRegex.test(newPassword)) {
      Swal.fire({
        icon: "error",
        title: "비밀번호 입력 오류",
        showConfirmButton: false,
        timer: 2000,
        text: "비밀번호는 4-20자 이내의 영문 숫자 조합이어야 합니다.",
      });
      return;
    }
    if (newPassword !== confirmPassword) {
      Swal.fire({
        icon: "error",
        title: "비밀번호 불일치",
        showConfirmButton: false,
        timer: 2000,
        text: "비밀번호가 일치하지 않습니다.",
      });
      return;
    }
    try {
      const response = await updatePassword(currentPassword, newPassword);
      if (response.status === 200) {
        handleSuccess("비밀번호 변경 성공하였습니다.");
        onSubmit();
      }
    } catch (e) {
      handleError(e);
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
        <h1 className={styles.title}>비밀번호 변경</h1>
        <div>
          <div className={styles.text}>현재 비밀번호</div>
          <input
            type="password"
            className={`${styles.input} ${styles.currentPassword}`}
            placeholder="현재 비밀번호를 입력해주세요"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
          />
        </div>
        <div>
          <div className={styles.text}>신규 비밀번호</div>
          <input
            type="password"
            className={styles.input}
            placeholder="신규 비밀번호를 입력해주세요"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
        </div>
        <div>
          <div className={styles.text}>비밀번호 확인</div>
          <input
            type="password"
            className={styles.input}
            placeholder="비밀번호를 확인해주세요"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
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

ChangePasswordModal.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ChangePasswordModal;
