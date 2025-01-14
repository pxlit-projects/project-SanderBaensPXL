import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { User } from '../model/user.model';
import { HttpHeaders } from '@angular/common/http';

describe('AuthService', () => {
  let service: AuthService;
  let localStorageSpy: jasmine.SpyObj<Storage>;

  beforeEach(() => {
    localStorageSpy = jasmine.createSpyObj('localStorage', ['getItem', 'setItem', 'removeItem']);
    spyOnProperty(window, 'localStorage').and.returnValue(localStorageSpy);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: Storage, useValue: localStorageSpy }
      ]
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('loadUserFromLocalStorage', () => {
    it('should load user data from localStorage', () => {
      localStorageSpy.getItem.and.returnValue('admin');
      service['loadUserFromLocalStorage']();
      expect(service['role']).toBe('admin');
    });

    it('should not set properties if localStorage is empty', () => {
      localStorageSpy.getItem.and.returnValue(null);
      service['loadUserFromLocalStorage']();
      expect(service['role']).toBe('');
    });
  });

  describe('getUser', () => {
    it('should return a User object with current properties', () => {
      service['role'] = 'admin';
      service['name'] = 'John';
      service['email'] = 'john@example.com';
      const user = service.getUser();
      expect(user).toEqual(jasmine.any(User));
      expect(user.role).toBe('admin');
      expect(user.name).toBe('John');
      expect(user.email).toBe('john@example.com');
    });
  });

  describe('login', () => {
    it('should set user properties and store in localStorage', () => {
      const user = new User('admin', 'John', 'john@example.com');
      service.login(user);
      expect(service['role']).toBe('admin');
      expect(service['name']).toBe('John');
      expect(service['email']).toBe('john@example.com');
      expect(localStorageSpy.setItem).toHaveBeenCalledWith('role', 'admin');
      expect(localStorageSpy.setItem).toHaveBeenCalledWith('name', 'John');
      expect(localStorageSpy.setItem).toHaveBeenCalledWith('email', 'john@example.com');
    });
  });

  describe('logout', () => {
    it('should clear user properties and remove from localStorage', () => {
      service.logout();
      expect(service['role']).toBe('');
      expect(service['name']).toBe('');
      expect(service['email']).toBe('');
      expect(localStorageSpy.removeItem).toHaveBeenCalledWith('role');
      expect(localStorageSpy.removeItem).toHaveBeenCalledWith('name');
      expect(localStorageSpy.removeItem).toHaveBeenCalledWith('email');
    });
  });

  describe('getHeaders', () => {
    it('should return HttpHeaders with user properties', () => {
      service['role'] = 'admin';
      service['name'] = 'John';
      service['email'] = 'john@example.com';
      const headers = service.getHeaders();
      expect(headers).toEqual(jasmine.any(HttpHeaders));
      expect(headers.get('X-Role')).toBe('admin');
      expect(headers.get('X-Name')).toBe('John');
      expect(headers.get('X-Email')).toBe('john@example.com');
    });
  });

  describe('checkLogin', () => {
    it('should return true if role is set', () => {
      service['role'] = 'admin';
      expect(service.checkLogin()).toBeTrue();
    });

    it('should return false if role is not set', () => {
      service['role'] = '';
      expect(service.checkLogin()).toBeFalse();
    });
  });

  describe('checkRole', () => {
    it('should return true if role matches', () => {
      service['role'] = 'admin';
      expect(service.checkRole('admin')).toBeTrue();
    });

    it('should return false if role does not match', () => {
      service['role'] = 'user';
      expect(service.checkRole('admin')).toBeFalse();
    });
  });

  describe('checkName', () => {
    it('should return true if name matches', () => {
      service['name'] = 'John';
      expect(service.checkName('John')).toBeTrue();
    });

    it('should return false if name does not match', () => {
      service['name'] = 'John';
      expect(service.checkName('Jane')).toBeFalse();
    });
  });
});
