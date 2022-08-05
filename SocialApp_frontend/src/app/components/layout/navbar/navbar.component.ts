import { Component, OnInit } from '@angular/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  public placeholder: string;

  constructor(private translate:TranslateService) {}

  ngOnInit(): void {
    const lang = localStorage.getItem('lang');
    if (lang != null) {
      this.translate.use(lang);
    } else {
      this.translate.use('en');
    }
    
    this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.translate.use(event.lang);
      this.translate.get('NAVBAR.SEARCH_PEOPLE').subscribe((res: string) => {
        this.placeholder = res;
      });
    });
  }

}
