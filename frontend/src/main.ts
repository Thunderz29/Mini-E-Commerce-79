import { importProvidersFrom } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { ForgotPasswordComponent } from './app/pages/forgot-password/forgot-password.component';
import { HomeComponent } from './app/pages/homepage/homepage.component';
import { LoginComponent } from './app/pages/login/login.component';
import { RegisterComponent } from './app/pages/register/register.component';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([
      { path: '', redirectTo: '/homepage', pathMatch: 'full' }, // Redirect default
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'home', component: HomeComponent}
    ]),
    importProvidersFrom(MatInputModule, MatButtonModule)
  ]
}).catch(err => console.error(err));
