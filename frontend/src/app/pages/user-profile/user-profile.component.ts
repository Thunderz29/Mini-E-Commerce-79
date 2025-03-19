import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  standalone: true,
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  imports: [CommonModule, FormsModule, NavbarComponent],
})
export class UserProfileComponent {
  user = {
    avatar: 'https://i.pravatar.cc/150',
    username: 'JohnDoe',
    email: 'johndoe@example.com',
    phone: '+62 812 3456 7890',
  };

  isEditing = false;
  editUser = { username: '', phone: '' };

  toggleEdit() {
    this.isEditing = !this.isEditing;
    if (this.isEditing) {
      // Menyalin data user ke form edit
      this.editUser = { username: this.user.username, phone: this.user.phone };
    }
  }

  saveChanges() {
    // Menyimpan perubahan
    this.user.username = this.editUser.username;
    this.user.phone = this.editUser.phone;
    this.isEditing = false;
  }
}
