import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'paginator-admin-comp',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.css']
})
export class PaginatorComponent implements OnInit {
  @Input() public name: string;
  @Input() public paginator: any;
  @Input() public route: string;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  public goToFirstPage() {
    if (this.route == 'users') {
      this.router.navigate([`/admin/${this.route}/${this.name}/0`]);
    }
    if (this.route == 'reports') {
      this.router.navigate([`/admin/${this.route}/0`]);
    }
  }

  public goToLastPage() {
    if (this.route == 'users') {
      this.router.navigate([`/admin/${this.route}/${this.name}/${this.paginator.totalPages-1}`]);
    }
    if (this.route == 'reports') {
      this.router.navigate([`/admin/${this.route}/${this.paginator.totalPages-1}`]);
    }
  }

  public goToPreviousPage() {
    if (this.route == 'users') {
      this.router.navigate([`/admin/${this.route}/${this.name}/${this.paginator.number-1}`]);
    }
    if (this.route == 'reports') {
      this.router.navigate([`/admin/${this.route}/${this.paginator.number-1}`]);
    }
  }

  public goToNextPage() {
    if (this.route == 'users') {
      this.router.navigate([`/admin/${this.route}/${this.name}/${this.paginator.number+1}`]);
    }
    if (this.route == 'reports') {
      this.router.navigate([`/admin/${this.route}/${this.paginator.number+1}`]);
    }
  }
}