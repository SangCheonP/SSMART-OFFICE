import React from "react";
import styles from "@/styles/Message/Message.module.css";
import Search from "@/assets/Common/Search.svg?react";

const SearchBar = ({ placeholder = "직원을 검색하세요" }) => {
  return (
    <div className={styles.search_bar_container}>
      <Search className={styles.search_icon} />
      <input
        type="text"
        className={styles.search_bar}
        placeholder={placeholder}
      />
    </div>
  );
};

export default SearchBar;
