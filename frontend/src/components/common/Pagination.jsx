import styles from "@/styles/MyPage/Pagination.module.css";
import { Link } from "react-router-dom";

const Pagination = ({ totalPages, currentPage, onPageChange }) => {
  const pageCount = 5;
  const start = Math.floor((currentPage - 1) / pageCount) * pageCount + 1;
  const noPrev = start === 1;
  const noNext = start + pageCount > totalPages;

  return (
    <div className={styles.wrapper}>
      <ul>
        <li
          className={`${styles.move} ${noPrev && styles.invisible}`}
          onClick={() => !noPrev && onPageChange(start - 1)}
        >
          이전
        </li>
        {[...Array(pageCount)].map((_, i) => {
          const pageNumber = start + i;
          return (
            pageNumber <= totalPages && (
              <li key={i}>
                <Link
                  className={`${styles.page} ${
                    currentPage === pageNumber && styles.active
                  }`}
                  to={`?page=${pageNumber}`}
                  onClick={() => onPageChange(pageNumber)}
                >
                  {pageNumber}
                </Link>
              </li>
            )
          );
        })}
        <li
          className={`${styles.move} ${noNext && styles.invisible}`}
          onClick={() => !noNext && onPageChange(start + pageCount)}
        >
          다음
        </li>
      </ul>
    </div>
  );
};

export default Pagination;
