import { useRef, useState } from "react";

import Profile from "@/assets/Common/profile.png";

import styles from "@/styles/ImageUpload.module.css";

const ImageUpload = () => {
  const [image, setImage] = useState(null);
  const fileInputRef = useRef(null);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => setImage(e.target.result);
      reader.readAsDataURL(file);
    }
  };

  const handleImageClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  return (
    <div onClick={handleImageClick}>
      <img
        src={image || Profile}
        alt="클릭하여 파일 선택"
        className={styles.image}
      />
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        className={styles.input}
      />
    </div>
  );
};

export default ImageUpload;
