import { useState, useEffect } from 'react';
import './App.css';
import { auth } from './assets/config/firebase';
import { useAuthState } from 'react-firebase-hooks/auth';
import { db } from './assets/config/firebase';
import { collection, doc, getDocs, deleteDoc } from 'firebase/firestore';
import Header from './components/Header';
import UserProfile from './components/UserProfile';
import { Task, User } from './types.ts';

function App() {
  const [count, setCount] = useState(0); // State to manage the counter
  const [user, setUser] = useState<User | null>(null); // State to manage authentication state
  const [tasks, setTasks] = useState<Task[]>([]); // State to store tasks fetched from Firestore

  const showAlert = (message: string) => window.alert(message); // Function to show alert messages

  useEffect(() => {
    const fetchTasks = async () => {
      if (auth.currentUser) {
        const userId = auth.currentUser.uid; // Get the current user's unique ID
        const usersRef = collection(db, 'users'); // Reference to the 'users' collection in Firestore
        const userDocRef = doc(usersRef, userId); // Reference to the specific user's document
        const tasksRef = collection(userDocRef, 'tasks'); // Reference to the 'tasks' subcollection

        // Fetch the tasks from the user's unique document
        const tasksSnapshot = await getDocs(tasksRef); // Fetch all documents in the 'tasks' subcollection
        console.log("Tasks Snapshot:", tasksSnapshot);

        // Map the tasks to an array of objects
        const tasksList = tasksSnapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
        console.log("Tasks List:", tasksList);
        setTasks(tasksList); // Update the state with the fetched tasks
      }
    };

    fetchTasks();
  }, [auth.currentUser]); // Dependency array ensures this runs when the user changes

  const deleteAllData = async () => {
    if (auth.currentUser) {
      const userId = auth.currentUser.uid; // Get the current user's unique ID
      const userDocRef = doc(db, 'users', userId); // Reference to the user's document

      try {
        // Delete all tasks in the 'tasks' subcollection
        const tasksRef = collection(userDocRef, 'tasks');
        const tasksSnapshot = await getDocs(tasksRef); // Fetch all tasks
        const deleteTasksPromises = tasksSnapshot.docs.map((taskDoc) => deleteDoc(taskDoc.ref)); // Create an array of Promises to delete each task
        await Promise.all(deleteTasksPromises); // Wait for all tasks to be deleted

        // Delete the user document
        await deleteDoc(userDocRef); // Delete the user's document from Firestore

        setTasks([]); // Clear tasks from the state to reflect changes in the UI
        await auth.signOut(); // Sign out the user after deleting their data

        showAlert('All data deleted successfully!');
      } catch (error) {
        console.error('Error deleting data:', error); // Log any errors
        showAlert('Failed to delete data. Please try again.');
      }
    } else {
      showAlert('No user is signed in.'); // Alert if no user is signed in
    }
  };

  return (
    <>      
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <p>
          Lolz it's a counter from the template I used to start this project
        </p>
      </div>
      
      <Header user={user} />
      {user && <UserProfile user={auth.currentUser} tasks={tasks} deleteAllData={deleteAllData} />}
    </>
  );
}

export default App;
