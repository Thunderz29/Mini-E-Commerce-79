import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  imports: [CommonModule, FormsModule],
})
export class UserProfileComponent {
  user = {
    avatar: 'https://i.pravatar.cc/150',
    username: 'JohnDoe',
    email: 'johndoe@example.com',
    phone: '+62 812 3456 7890',
  };

  isEditing = false;
  newUsername = this.user.username;

  toggleEdit() {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      this.user.username = this.newUsername;
    }
  }
}
