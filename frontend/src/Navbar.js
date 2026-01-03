import React from 'react';

function Navbar({ user, onLogout }) {
  return (
    <div className="navbar">
      <h1>MedConnect</h1>
      <div className="navbar-right">
        <span>Welcome, {user.fullName}</span>
        <button onClick={onLogout} className="btn btn-secondary">
          Logout
        </button>
      </div>
    </div>
  );
}

export default Navbar;
