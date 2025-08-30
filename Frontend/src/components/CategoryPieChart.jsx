import React from 'react';
import { Pie } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(ArcElement, Tooltip, Legend);

const CategoryPieChart = ({ data }) => {
  const chartData = {
    labels: data.map(item => item.categoryName),
    datasets: [
      {
        label: 'Spending by Category',
        data: data.map(item => parseFloat(item.totalSpent)),
        backgroundColor: [
          '#FF6384', '#36A2EB', '#FFCE56', '#8E44AD', '#2ECC71',
          '#F39C12', '#D35400', '#1ABC9C', '#C0392B'
        ],
        borderWidth: 1,
      },
    ],
  };

  return (
    <div style={{ maxWidth: '400px', margin: 'auto' }}>
      <Pie
        data={chartData}
      
      />
    </div>
  );
};

export default CategoryPieChart;
