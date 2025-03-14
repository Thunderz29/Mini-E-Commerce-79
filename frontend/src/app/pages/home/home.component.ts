import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FooterComponent } from '../../components/footer/footer.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { ProductListComponent } from '../../components/product-list/product-list.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, NavbarComponent, ProductListComponent, FooterComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  @ViewChild(ProductListComponent) productList!: ProductListComponent;

  onSearch(searchTerm: string) {
    this.productList.filterProducts(searchTerm);
  }
}
