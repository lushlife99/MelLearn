import { Outlet, useLocation } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { Suspense } from 'react';
import NavBar from './components/NavBar';

export default function App() {
  const location = useLocation();

  const hideNav = ['/login', '/callback', '/signup', '/spotify-login'].includes(
    location.pathname
  );
  return (
    <>
      <Toaster position='top-right' toastOptions={{ duration: 3000 }} />
      <Suspense>
        <Outlet />
        {!hideNav && <NavBar />}
      </Suspense>
    </>
  );
}
