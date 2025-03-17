import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FooterComponent } from '../../components/footer/footer.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { NotificationComponent } from '../../components/notification/notification.component';
import { ProductListComponent } from '../../components/product-list/product-list.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, NavbarComponent, ProductListComponent, FooterComponent, NotificationComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit{
  showSuccessMessage: boolean = false;
  userName: string = 'User';

  ngOnInit() {
      setTimeout(() => {
        this.showSuccessMessage = false;
      }, 3000);
    
  }

  @ViewChild(ProductListComponent) productList!: ProductListComponent;

  onSearch(searchTerm: string) {
    this.productList.filterProducts(searchTerm);
  }
}
