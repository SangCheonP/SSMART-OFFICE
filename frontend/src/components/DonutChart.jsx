import styles from "./../styles/DonutChart.module.css";

function DonutChart() {
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
          stroke="#4876EF"
          strokeWidth="20"
          strokeLinecap="round"
          strokeDasharray={`${2 * Math.PI * 90 * 0.5} ${
            2 * Math.PI * 90 * 0.5
          }`}
          strokeDashoffset={2 * Math.PI * 90 * 0.25}
        />
        <text x="100" y="95" textAnchor="middle" fontSize="20" fill="#000">
          <tspan fontSize="20" fontWeight="bold">
            3
          </tspan>
          <tspan fontSize="14" dx="2">
            석 사용가능
          </tspan>
        </text>
        <text x="100" y="125" textAnchor="middle" fontSize="12" fill="#000">
          <tspan fontSize="14">총</tspan>
          <tspan fontSize="20" dx="2" fontWeight="bold">
            6
          </tspan>
          <tspan fontSize="14" dx="2">
            석
          </tspan>
        </text>
      </svg>
    </div>
  );
}
export default DonutChart;
