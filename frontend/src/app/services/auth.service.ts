import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginDTO } from '../models/login.model';
import { RegisterDTO } from '../models/register.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrlLogin = 'http://localhost:8080/user-service/auth/login';
  private apiUrlRegister = 'http://localhost:8080/user-service/users';
  private http = inject(HttpClient);

  login(user: LoginDTO): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    };
    return this.http.post<any>(this.apiUrlLogin, user, httpOptions);
  }

  register(user: RegisterDTO): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    };
    return this.http.post<any>(this.apiUrlRegister, user, httpOptions);
  }
}