import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";

import styles from "@/styles/Modals/ChangePasswordModal.module.css";

const ChangePasswordModal = ({ onSubmit, onClose }) => {
  const handleClickSubmit = () => {
    onSubmit();
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
          />
        </div>
        <div>
          <div className={styles.text}>신규 비밀번호</div>
          <input
            type="password"
            className={styles.input}
            placeholder="신규 비밀번호를 입력해주세요"
          />
        </div>
        <div>
          <div className={styles.text}>비밀번호 확인</div>
          <input
            type="password"
            className={styles.input}
            placeholder="비밀번호를 확인해주세요"
          />
        </div>
        <div className={styles.buttonBox}>
          <button
            onClick={handleClickCancel}
            className={`${styles.cancel} ${styles.button}`}
          >
            취소
          </button>
          <button
            onClick={handleClickSubmit}
            className={`${styles.confirm} ${styles.button}`}
          >
            수정
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
