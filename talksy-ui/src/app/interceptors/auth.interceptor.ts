import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { TokenService } from '../../token.service';
import { AuthenticationService } from '../services/services';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  // Skip adding token for auth endpoints
  const isAuthEndpoint = req.url.includes('/api/v1/auth/login') || 
                         req.url.includes('/api/v1/auth/register') ||
                         req.url.includes('/api/v1/auth/confirm-account') ||
                         req.url.includes('/api/v1/auth/refresh');

  // Clone request and add Authorization header if token exists
  let authReq = req;
  if (!isAuthEndpoint) {
    const token = tokenService.getAccessToken();
    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 or 403 and not already on auth endpoint, try to refresh token
      if ((error.status === 401 || error.status === 403) && !isAuthEndpoint) {
        const refreshToken = tokenService.getRefreshToken();
        
        if (refreshToken) {
          // Attempt to refresh the token
          return authService.refresh({
            body: { refreshToken }
          }).pipe(
            switchMap((response) => {
              // Store new tokens
              if (response.access_token && response.refresh_token) {
                tokenService.setTokens(response.access_token, response.refresh_token);
                
                // Retry the original request with new token
                const retryReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${response.access_token}`
                  }
                });
                return next(retryReq);
              }
              
              // If no tokens in response, clear and redirect
              return throwError(() => error);
            }),
            catchError((refreshError) => {
              // Refresh failed, clear tokens and redirect to login

              return throwError(() => refreshError);
            })
          );
        } else {
          // No refresh token available, redirect to login
          tokenService.clearTokens();
          router.navigate(['/login']);
        }
      }
      
      return throwError(() => error);
    })
  );
};
