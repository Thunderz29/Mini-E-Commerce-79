import { Routes } from '@angular/router';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
// import { OrdersComponent } from './pages/orders/orders.component';
import { UserProfileComponent } from './pages/user-profile/user-profile.component';
// import { CartComponent } from './pages/cart/cart.component';
import { ProductDetailComponent } from './pages/product-detail/product-detail.component';

export const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  // { path: 'cart', component: CartComponent },
  // { path: 'orders', component: OrdersComponent },
  { path: 'profile', component: UserProfileComponent },
  { path: 'product', component: ProductDetailComponent },

  ];
