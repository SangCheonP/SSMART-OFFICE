import { PropTypes } from "prop-types";
import ReactModal from "react-modal";

import Close from "@/assets/Modals/Close.svg?react";
import Profile from "@/assets/Common/Profile.png";
import styles from "@/styles/Modals/AddMemberModal.module.css";
import api from "@/services/api";

const AddMemberModal = ({ onSubmit, onClose }) => {
  const handleClickSubmit = () => {
    try {
      const response = api.get("/users/me");
      console.log(response);

      onSubmit();
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
        <h1 className={styles.title}>사원 정보 등록</h1>
        <div className={styles.imageBox}>
          <img src={Profile} alt="" className={styles.image} />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="name" className={styles.text}>
            이름
          </label>
          <input type="text" id="name" className={styles.input} />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="name" className={styles.text}>
            직책
          </label>
          <input type="text" id="name" className={styles.input} />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="name" className={styles.text}>
            직무
          </label>
          <input type="text" id="name" className={styles.input} />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="name" className={styles.text}>
            이메일
          </label>
          <input type="text" id="name" className={styles.input} />
        </div>
        <div className={styles.textBox}>
          <label htmlFor="name" className={styles.text}>
            사원번호
          </label>
          <input type="text" id="name" className={styles.input} />
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

AddMemberModal.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};
export default AddMemberModal;
