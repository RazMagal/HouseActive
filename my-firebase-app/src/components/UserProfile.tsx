import React from 'react';
import TaskList from './TaskList';
import { User, Task } from '../types';
import { auth } from '../assets/config/firebase';

interface UserProfileProps {
  user: User;
  tasks: Task[];
  deleteAllData: () => void;
}

const UserProfile: React.FC<UserProfileProps> = ({ user, tasks, deleteAllData }) => (
  <div>
    <h1><p>{user?.displayName}</p></h1>
    <img src={user?.photoURL || ''} width="100" height="100" alt="Profile" />
    <p>{user?.email}</p>
    <p>Your user UID is: {user?.uid}</p>
    <button onClick={() => auth.signOut()}>Sign Out</button>
    <h2>Your Tasks</h2>
    <TaskList tasks={tasks} />
    <button onClick={deleteAllData} style={{ marginTop: '20px', backgroundColor: 'red', color: 'white' }}>
      Delete All Data
    </button>
  </div>
);

export default UserProfile;