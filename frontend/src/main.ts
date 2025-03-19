import { provideHttpClient, withFetch } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { CartComponent } from './app/pages/cart/cart.component';
import { ForgotPasswordComponent } from './app/pages/forgot-password/forgot-password.component';
import { HomeComponent } from './app/pages/home/home.component';
import { LoginComponent } from './app/pages/login/login.component';
import { OrderHistoryComponent } from './app/pages/order-history/order-history.component';
import { OrderStatusComponent } from './app/pages/order-status/order-status.component';
import { PaymentComponent } from './app/pages/payment/payment.component';
import { ProductDetailComponent } from './app/pages/product-detail/product-detail.component';
import { RegisterComponent } from './app/pages/register/register.component';
import { UserProfileComponent } from './app/pages/user-profile/user-profile.component';

// Angular Material
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';

// Forms
import { FormsModule } from '@angular/forms';

// Import AuthGuard
import { AuthGuard } from './app/guards/auth.guard';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([
      { path: '', redirectTo: '/login', pathMatch: 'full' },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'profile', component: UserProfileComponent },
      { path: 'cart', component: CartComponent },
      { path: 'order-history', component: OrderHistoryComponent },
      { path: 'payment', component: PaymentComponent },
      { path: 'order-status', component: OrderStatusComponent },
      { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
      { path: 'product', component: ProductDetailComponent, canActivate: [AuthGuard] }
    ]),
    provideHttpClient(withFetch()),
    importProvidersFrom(MatInputModule, MatButtonModule, FormsModule)
  ]
}).catch(err => console.error(err));
