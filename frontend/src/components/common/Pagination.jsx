import styles from "@/styles/MyPage/Pagination.module.css";

const Pagination = ({ totalPages, currentPage, onPageChange }) => {
  const pageCount = 5; // 표시할 페이지 수
  const startPage = Math.floor((currentPage - 1) / pageCount) * pageCount + 1;
  const endPage = Math.min(startPage + pageCount - 1, totalPages);

  const handlePageClick = (page) => {
    if (page >= 1 && page <= totalPages) {
      onPageChange(page);
    }
  };

  return (
    <div className={styles.wrapper}>
      <button
        className={styles.prev}
        disabled={currentPage === 1}
        onClick={() => handlePageClick(currentPage - 1)}
      >
        이전
      </button>
      {Array.from(
        { length: endPage - startPage + 1 },
        (_, i) => startPage + i
      ).map((page) => (
        <button
          key={page}
          className={`${styles.page} ${currentPage === page && styles.active}`}
          onClick={() => handlePageClick(page)}
        >
          {page}
        </button>
      ))}
      <button
        className={styles.next}
        disabled={currentPage === totalPages}
        onClick={() => handlePageClick(currentPage + 1)}
      >
        다음
      </button>
    </div>
  );
};

export default Pagination;
