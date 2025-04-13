import React from 'react';
import SignInButton from './SignInButton';

interface HeaderProps {
  user?: any; // Replace 'any' with a specific type if you know the structure of 'user'
}

const Header: React.FC<HeaderProps> = ({ user }) => (
  <div>
    <h1>Welcome to HouseActive</h1>
    {!user && <SignInButton />}
  </div>
);

export default Header;