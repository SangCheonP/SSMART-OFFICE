import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";

import styles from "@/styles/Modals/ChangeInfoModal.module.css";

const ChangeInfoModal = ({ onSubmit, onClose }) => {
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
        <h1 className={styles.title}>개인정보 수정</h1>
        <div>
          <div className={styles.text}>이름</div>
          <input
            type="text"
            className={`${styles.input} ${styles.readOnly}`}
            readOnly
            value={"김성민"}
          />
        </div>
        <div>
          <div className={styles.text}>이메일</div>
          <input
            type="text"
            className={`${styles.input} ${styles.readOnly}`}
            readOnly
          />
        </div>
        <div>
          <div className={styles.text}>휴대전화</div>
          <input
            type="password"
            className={styles.input}
            placeholder="연락처를 입력해주세요"
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

ChangeInfoModal.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ChangeInfoModal;
