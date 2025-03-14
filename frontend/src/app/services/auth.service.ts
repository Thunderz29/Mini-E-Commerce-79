import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginDTO } from '../models/login.model';
import { RegisterDTO } from '../models/register.model';
import { ForgotPasswordDTO } from '../models/forgot-password.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrlLogin = 'http://localhost:8080/user-service/auth/login';
  private apiUrlRegister = 'http://localhost:8080/user-service/users';
  private apiUrlResetPassword = 'http://localhost:8080/user-service/auth/forgot-password';

  private http = inject(HttpClient);

  login(user: LoginDTO): Observable<any> {
    return this.http.post<any>(this.apiUrlLogin, user, this.httpOptions());
  }

  register(user: RegisterDTO): Observable<any> {
    return this.http.post<any>(this.apiUrlRegister, user, this.httpOptions());
  }

  resetPassword(data: ForgotPasswordDTO): Observable<any> {
    return this.http.post<any>(this.apiUrlResetPassword, data, this.httpOptions());
  }

  private httpOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    };
  }
}
