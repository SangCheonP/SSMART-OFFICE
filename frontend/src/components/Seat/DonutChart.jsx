import PropTypes from "prop-types";
import styles from "@/styles/Seat/DonutChart.module.css";

const DonutChart = ({ number, totalNumber }) => {
  let percentage = number / totalNumber;

  return (
    <div className={styles.container}>
      <svg viewBox="0 0 200 200" width="100%" height="100%">
        <circle
          cx="100"
          cy="100"
          r="90"
          fill="none"
          stroke="#d9d9d9"
          strokeWidth="20"
        />
        <circle
          cx="100"
          cy="100"
          r="90"
          fill="none"
          stroke="var(--blue)"
          strokeWidth="20"
          strokeLinecap="round"
          strokeDasharray={`${2 * Math.PI * 90 * percentage} ${
            2 * Math.PI * 90 * (1 - percentage)
          }`}
          strokeDashoffset={2 * Math.PI * 90 * 0.25}
        />
        <text x="100" y="95" textAnchor="middle" fontSize="20" fill="#000">
          <tspan fontSize="20" fontWeight="bold">
            {totalNumber - number}
          </tspan>
          <tspan fontSize="14" dx="2">
            석 사용가능
          </tspan>
        </text>
        <text x="100" y="125" textAnchor="middle" fontSize="12" fill="#000">
          <tspan fontSize="14">총</tspan>
          <tspan fontSize="20" dx="2" fontWeight="bold">
            {totalNumber}
          </tspan>
          <tspan fontSize="14" dx="2">
            석
          </tspan>
        </text>
      </svg>
    </div>
  );
};

DonutChart.propTypes = {
  number: PropTypes.number.isRequired,
  totalNumber: PropTypes.number.isRequired,
};

export default DonutChart;
