import { useEffect, useRef, useState } from "react";

import Profile from "@/assets/Common/Profile.png";

import styles from "@/styles/ImageUpload.module.css";
import PropTypes from "prop-types";

const ImageUpload = ({ onImageSelect, classNameValue, defaultImage }) => {
  const [image, setImage] = useState(null);
  const fileInputRef = useRef(null);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => setImage(e.target.result);
      reader.readAsDataURL(file);

      onImageSelect(file);
    }
  };

  const handleImageClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };
  useEffect(() => {
    setImage(defaultImage);
  }, [defaultImage]);

  return (
    <div onClick={handleImageClick} className={classNameValue}>
      <img
        src={image ? image : Profile}
        alt="클릭하여 파일 선택"
        className={styles.image}
      />
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        className={styles.input}
        accept="image/*"
      />
    </div>
  );
};

ImageUpload.propTypes = {
  onImageSelect: PropTypes.file,
  classNameValue: PropTypes.string,
  defaultImage: PropTypes.string,
};

export default ImageUpload;
