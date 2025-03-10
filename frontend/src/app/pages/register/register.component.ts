import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RegisterDTO } from '../../models/register.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, HttpClientModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  user: RegisterDTO = {
    username: '',
    email: '',
    password: '',
    phone: ''
  };
  constructor(private authService: AuthService) {}

  onRegister() {
    this.authService.register(this.user).subscribe(
      (response) => {
        alert('Registrasi berhasil!');
        console.log(response);
      },
      (error) => {
        alert('Registrasi gagal!');
        console.error(error);
      }
    );
  }
}
