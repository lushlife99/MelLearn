import { createBrowserRouter } from 'react-router-dom';
import { ROUTES } from './routes';
import LoginPage from '@/pages/auth/LoginPage';
import SignupPage from '@/pages/auth/SignupPage';
import App from '@/App';
import SpotifyCallbackPage from '@/features/spotify/SpotifyCallbackPage';
import SpotifyLoginPage from '@/features/spotify/SpotifyLoginPage';
import NotFound from '@/pages/NotFound';
import HomePage from '@/pages/HomePage';
import ArtistListPage from '@/pages/artist/ArtistListPage';
import ArtistDetailPage from '@/pages/artist/ArtistDetailPage';
import TrackListPage from '@/pages/track/TrackListPage';
import TrackDetailPage from '@/pages/track/TrackDetailPage';
import QuizPage from '@/pages/quiz/QuizPage';
import ReadingQuizPage from '@/pages/quiz/ReadingQuizPage';

export const router = createBrowserRouter([
  {
    path: ROUTES.HOME,
    element: <App />,
    children: [
      {
        path: ROUTES.HOME,
        element: <HomePage />,
      },

      {
        path: ROUTES.LOGIN,
        element: <LoginPage />,
      },
      {
        path: ROUTES.SIGNUP,
        element: <SignupPage />,
      },
      {
        path: ROUTES.SPOTIFY_LOGIN,
        element: <SpotifyLoginPage />,
      },
      {
        path: ROUTES.SPOTIFY_CALLBACK,
        element: <SpotifyCallbackPage />,
      },
      {
        path: ROUTES.ARTISTS,
        element: <ArtistListPage />,
      },
      {
        path: ROUTES.ARTIST_DETAIL(':id'),
        element: <ArtistDetailPage />,
      },
      {
        path: ROUTES.TRACKS,
        element: <TrackListPage />,
      },
      {
        path: ROUTES.TRACK_DETAIL(':id'),
        element: <TrackDetailPage />,
      },
      {
        path: ROUTES.QUIZ(':id'),
        element: <QuizPage />,
      },
      {
        path: ROUTES.QUIZ_READING(':id'),
        element: <ReadingQuizPage />,
      },
      {
        path: '*',
        element: <NotFound />,
      },
    ],
  },
]);
