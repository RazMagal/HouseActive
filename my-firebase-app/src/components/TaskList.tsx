import React from 'react';

interface Task {
  id: string;
  name?: string;
}

interface TaskListProps {
  tasks: Task[];
}

const TaskList: React.FC<TaskListProps> = ({ tasks }) => (
  <ul>
    {tasks.map((task) => (
      <li key={task.id}>{task.name || 'Unnamed Task'}</li>
    ))}
  </ul>
);

export default TaskList;