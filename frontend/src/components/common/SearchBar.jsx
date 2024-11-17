import React, { useState } from "react";
import styles from "@/styles/Message/Message.module.css";
import Search from "@/assets/Common/Search.svg?react";

const SearchBar = ({ placeholder = "직원을 검색하세요", onSearch }) => {
  const [query, setQuery] = useState("");

  const handleChange = (event) => {
    const value = event.target.value;
    setQuery(value);
    onSearch(value); // 부모 컴포넌트로 검색어 전달
  };

  return (
    <div className={styles.search_bar_container}>
      <Search className={styles.search_icon} />
      <input
        type="text"
        className={styles.search_bar}
        placeholder={placeholder}
        value={query}
        onChange={handleChange}
      />
    </div>
  );
};

export default SearchBar;
