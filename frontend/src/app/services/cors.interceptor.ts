import { HttpInterceptorFn } from '@angular/common/http';

export const corsInterceptor: HttpInterceptorFn = (req, next) => {
  const modifiedReq = req.clone({
    withCredentials: true,
    headers: req.headers.set('Content-Type', 'application/json')
  });
  return next(modifiedReq);
};