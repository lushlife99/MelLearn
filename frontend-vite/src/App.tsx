import { Outlet, useLocation } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { Suspense } from 'react';
import NavBar from './components/NavBar';
import useSpotifySDKSetup from './features/spotify/hooks/useSpotifySDKSetup';

export default function App() {
  const location = useLocation();

  const hideNav = ['/login', '/callback', '/signup', '/spotify-login'].includes(
    location.pathname
  );
  const token = localStorage.getItem('spotify_access_token');
  useSpotifySDKSetup(token, !hideNav);
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
