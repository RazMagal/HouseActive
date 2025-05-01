import React from 'react';
import SignInButton from './SignInButton';
import { User } from '../types.ts';

interface HeaderProps {
  user?: User;
}

const Header: React.FC<HeaderProps> = ({ user }) => (
  <div>
    <h1>Welcome to HouseActive</h1>
    {!user && <SignInButton />}
  </div>
);

export default Header;