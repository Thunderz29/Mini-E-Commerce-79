import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthButtonComponent } from '../../components/auth-button/auth-button.component';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    AuthCardComponent,
    AuthButtonComponent
  ],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  email: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  onResetPassword() {
    this.errorMessage = ''; // Reset pesan error

    if (!this.email || !this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Semua kolom harus diisi!';
      return;
    }

    if (!this.email.includes('@')) {
      this.errorMessage = 'Format email tidak valid!';
      return;
    }

    if (this.newPassword.length < 6) {
      this.errorMessage = 'Password minimal 6 karakter!';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Konfirmasi password tidak cocok!';
      return;
    }

    this.isLoading = true;

    // this.authService.resetPassword({ email: this.email, newPassword: this.newPassword }).subscribe({
    //   next: () => {
    //     alert('Password berhasil direset!');
    //     this.router.navigate(['/login']);
    //   },
    //   error: (err) => {
    //     this.errorMessage = err.error?.message || 'Terjadi kesalahan, coba lagi.';
    //   },
    //   complete: () => {
    //     this.isLoading = false;
    //   }
    // });
  }
}
